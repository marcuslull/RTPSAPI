import { Firehose } from "@skyware/firehose";
import * as redis from "redis";


async function main() {




    // Redis connection
    const redisConnection = redis.createClient();

    redisConnection.on("error", err => {
        console.error("Firehose Connection Script - Redis Client Error: ", err)
        process.exit(1);
    });

    try {
        await redisConnection.connect();
        console.log("Firehose Connection Script - Redis Client Connected!")
    } catch (err) {
        console.error("Firehose Connection Script - Redis Client Connection Error: ", err)
        process.exit(1);
    }





    // Bluesky connection
    const firehoseConnection = new Firehose();

    firehoseConnection.on('error', (err) => {
        console.error("Firehose Connection Script - Firehose Connection Error:", err)
        process.exit(1);
    });

    firehoseConnection.on("commit", async commit => {

        const redisStream = "bskyFirehose"; // Redis stream for raw messaged
        const redisIdStrategy = "*"; // Redis auto-generated ID strategy

        try {
            // compose message
            let combined = JSON.stringify({
                repo: commit.repo,
                ops: commit.ops[0]
            });

            // add message to Redis stream named "bskyFirehose" and auto generate ids
            await redisConnection.xAdd(redisStream, redisIdStrategy, { combined })
            // console.log("Firehose Connection Script - Message added successfully! " + combined.toString())
        } catch (err) {
            console.error("Firehose Connection Script - Error adding message to stream: ", err);
        }
    });






    // Start the firehose
    try {
        await firehoseConnection.start();
        console.log("Firehose Connection Script - Firehose Started")
    } catch (err) {
        console.error("Firehose Connection Script - Error starting Bluesky Firehose:", err);
        await redisConnection.quit();
        process.exit(1);
    }





    // Termination firehose, redisConnection and process
    process.on('SIGTERM', handleTerminationSignal);
    process.on('SIGINT', handleTerminationSignal);

    async function handleTerminationSignal() {
        try {
            console.log("Firehose Connection Script - Shutting Down Firehose")
            await firehoseConnection.close(); // close the firehose
            firehoseConnection.on("close", async () => {
                await redisConnection.quit(); // close the Redis connection
            })
        } catch (err) {
            console.log("Firehose Connection Script - Error in terminating the Bluesky firehose script: ", err)
        } finally {
            process.exit(); // close the script
        }
    }
}

main()
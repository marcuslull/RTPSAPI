import { Firehose } from "@skyware/firehose";
import * as redis from "redis";


async function main() {

    // Redis connection
    const producer = redis.createClient();

    // handle errors
    producer.on("error", err => {
        console.log("Redis Client Error: ", err)
    });

    // connect
    await producer.connect();




    // Bluesky connection
    const firehose = new Firehose();

    firehose.on("commit", async commit => {
        let combined = {
            repo: commit.repo,
            ops: commit.ops[0]
        }

        try {
            await producer.xAdd("bskyFirehose", "*", "posts", JSON.stringify(combined)) // adding to Redis stream
            // console.log("Message added successfully!")
        } catch (err) {
            console.log("Error adding message to stream: ", err);
        }
        // console.log(JSON.stringify(combined));
    });

    firehose.start();




    // Termination
    process.on('SIGTERM', handleTerminationSignal);
    process.on('SIGINT', handleTerminationSignal);

    async function handleTerminationSignal() {
        try {
            await firehose.close();
            firehose.on("close", async () => {
                await producer.quit();
            })
        } catch (err) {
            console.log("Error in terminating the Bluesky firehose script: ", err)
        } finally {
            process.exit();
        }
    }
}

main();
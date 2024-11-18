import { Firehose } from "@skyware/firehose";

const firehose = new Firehose();
firehose.on("commit", (commit) => {
    let combined = {
        repo: commit.repo,
        ops: commit.ops[0]
    }
    // console.log(JSON.stringify(commit.ops[0]));
    console.log(JSON.stringify(combined));
});
firehose.start();
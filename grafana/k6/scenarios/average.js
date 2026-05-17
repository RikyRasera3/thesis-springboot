import {sleep} from "k6";
import {searchAccounts, getAccountById, createAccount, deleteAccount, patchAccount} from "../requests/accounts.js";
import {randomAccountPayload} from "../utils/dtoHelpers.js";

const scenarioTag = {scenario: "average"};

export const options = {
    stages: [
        {duration: "2m", target: 50},
        {duration: "5m", target: 50},
        {duration: "2m", target: 0}
    ],
    thresholds: {
        http_req_duration: [
            "p(95)<500",
            "p(99)<1000"
        ],
        http_req_failed: ["rate<0.01"]
   },
    tags: {
        project: "springboot",
        ...scenarioTag
    }
};

export default function () {
    // Search a random page wit size 20
    searchAccounts(scenarioTag);
    sleep(1);

    // Create a random account
    const account = JSON.parse(createAccount(randomAccountPayload(), scenarioTag).body);

    if (!account?.id) {
        return;
    }

    sleep(1);

    // Search the account previously created
    getAccountById(account.id, scenarioTag);
    sleep(1);

    // Update account previously created
    patchAccount(account.id, randomAccountPayload(), scenarioTag);
    sleep(1);

    // Delete account previously created
    deleteAccount(account.id, scenarioTag)
    sleep(1);
}
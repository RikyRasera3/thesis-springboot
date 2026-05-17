import {sleep} from "k6";
import {createAccount, deleteAccount, getAccountById, patchAccount, searchAccounts} from "../requests/accounts.js";
import {randomAccountPayload} from "../utils/dtoHelpers.js";

export const options = {
    stages: [
        {duration: "5m", target: 50},
        {duration: "30m", target: 50},
        {duration: "5m", target: 0}
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],
        http_req_failed: ["rate<0.01"]
    },
    tags: {
        project: "springboot",
        scenario: "soak"
    }
};

export default function () {
    // Search a random page wit size 20
    searchAccounts();
    sleep(1);

    // Create a random account
    const account = JSON.parse(createAccount(randomAccountPayload()).body);

    if (!account?.id) {
        return;
    }

    sleep(1);

    // Search the account previously created
    getAccountById(account.id);
    sleep(1);

    // Update account previously created
    patchAccount(account.id, randomAccountPayload());
    sleep(1);

    // Delete account previously created
    deleteAccount(account.id)
    sleep(1);
}
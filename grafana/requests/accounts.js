import http from "k6/http";
import {check} from "k6";
import {BASE_URL, HEADERS} from "../utils/config.js";
import {createdHandler, noContentHandler, successHandler} from "../utils/responseHandler.js";

const ACCOUNTS_PATH = "/accounts";

export function searchAccounts() {
    const page = Math.floor(Math.random() * 1001)

    const res = http.get(`${BASE_URL}${ACCOUNTS_PATH}/search?page=${page}&size=20`, {
        headers: HEADERS,
        tags: {name: "SearchAccounts"}
    });

    check(res, {
        "GET /accounts/search status 200": successHandler
    });

    return res;
}

export function getAccountById(id) {
    const res = http.get(`${BASE_URL}${ACCOUNTS_PATH}/${id}`, {
        headers: HEADERS,
        tags: {name: "GetAccountById"}
    });

    check(res, {"GET /accounts/{id} status 200": successHandler});

    return res;
}

export function createAccount(payload) {
    const res = http.post(`${BASE_URL}${ACCOUNTS_PATH}`, JSON.stringify(payload), {
        headers: HEADERS,
        tags: {name: "CreateAccount"}
    });

    check(res, {
        "POST /accounts status 201": createdHandler
    });

    return res;
}

export function patchAccount(id, payload) {
    const res = http.patch(`${BASE_URL}${ACCOUNTS_PATH}/${id}`, JSON.stringify(payload), {
        headers: HEADERS,
        tags: {name: "PatchAccount"}
    });

    check(res, {
        "PATCH /accounts/{id} status 200": successHandler
    });

    return res;
}

export function deleteAccount(id) {
    const res = http.del(`${BASE_URL}${ACCOUNTS_PATH}/${id}`, null, {
        headers: HEADERS,
        tags: {name: "DeleteAccount"}
    });

    check(res, {
        "DELETE /accounts/{id} status 204": noContentHandler
    });

    return res;
}
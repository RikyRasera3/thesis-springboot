const ROLE_IDS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

function randomDateOfBirth() {
    const start = new Date(1950, 0, 1).getTime();
    const end = new Date(2000, 0, 1).getTime();
    return new Date(start + Math.random() * (end - start)).toISOString().split("T")[0];
}

export function randomAccountPayload() {
    const randomString = Math.random().toString(36).substring(7);
    const count = Math.floor(Math.random() * 3) + 1;
    const roleIds = [...ROLE_IDS].sort(() => Math.random() - 0.5).slice(0, count);

    return {
        name: `NAME_${randomString}`,
        surname: `SURNAME_${randomString}`,
        email: `${randomString}@mail.com`,
        phone: `${randomString}`,
        dateOfBirth: randomDateOfBirth(),
        roleIds
    };
}
/** =============================================================================
* Seed data for thesis schema:
* - 10 role records
* - 100 000 account records
* - 200 000 account_role records:
*       - 70 % of accounts -> 1 role (accounts 1 - 70.000) = 70.000 rows
*       - 20 % of accounts -> 2 roles (accounts 70.001 - 90.000) = 40.000 rows
*       - 10 % of accounts -> 9 roles (accounts 90.001 - 100.000) = 90.000 rows
============================================================================= */

-- -----------------------------------------------------------------------------
-- role (10 records)
-- -----------------------------------------------------------------------------
INSERT INTO thesis.role(code, description) VALUES
    ('GLOBAL_ADMIN', 'Full access to all features'),
    ('USER_ADMIN', 'Manage users and groups'),
    ('APP_ADMIN', 'Manage application settings'),
    ('CLOUD_APP_ADMIN', 'Manage cloud applications'),
    ('DIR_READERS', 'Read directory data'),
    ('SECURITY_ADMIN', 'Manage security settings'),
    ('COMPLIANCE_ADMIN', 'Manage compliance policies'),
    ('EXCHANGE_ADMIN', 'Manage Exchange settings'),
    ('SHAREPOINT_ADMIN', 'Manage SharePoint resources'),
    ('HELPDESK_ADMIN', 'Manage helpdesk operations');

-- -----------------------------------------------------------------------------
-- account (100.000 records)
-- Names, emails and phone numbers are generated deterministically from the
-- series index so the dataset is reproducible without external tooling.
-- date_of_birth spans a ~50-year window (1955-01-01 to 2004-12-31).
-- -----------------------------------------------------------------------------
INSERT INTO thesis.account(name, surname, email, phone, date_of_birth)
SELECT
    'Name_' || i,
    'Surname_' || i,
    'account' || i || '@example.com',
    '3' || LPAD(i::text, 9, '0'),
    (DATE '1955-01-01' + ((i * 137) % 18262) * INTERVAL '1 day')::date
FROM generate_series(1, 100000) AS s(i);

-- -----------------------------------------------------------------------------
-- account_role - Group 1: 70 % of accounts -> 1 role each (70.000 rows)
-- Role is spread evenly across all 10 roles using modulo.
-- -----------------------------------------------------------------------------
INSERT INTO thesis.account_role(account_id, role_id)
SELECT
    s.i,
    (s.i % 10 + 1)::bigint
FROM generate_series(1, 70000) AS s(i);

-- -----------------------------------------------------------------------------
-- account_role - Group 2: 20 % of accounts -> 2 roles each (40.000 rows)
-- Two distinct roles per account: offsets +0 and +3 (mod 10) are always different
-- since gcd(3, 10) != 0.
-- -----------------------------------------------------------------------------
INSERT INTO thesis.account_role(account_id, role_id)
SELECT
    s.i,
    ((s.i + r.role_offset) % 10 + 1)::bigint
FROM generate_series(70001, 90000) AS s(i)
CROSS JOIN (VALUES (0), (3)) AS r(role_offset);

-- -----------------------------------------------------------------------------
-- account_role - Group 3: 10 % of accounts -> 9 roles each (90.000 rows)
-- Every account in this group receives roles 1-9.
-- -----------------------------------------------------------------------------
INSERT INTO thesis.account_role(account_id, role_id)
SELECT
    s.i,
    r.role_num::bigint
FROM generate_series(90001, 100000) AS s(i)
CROSS JOIN generate_series(1, 9) AS r(role_num);
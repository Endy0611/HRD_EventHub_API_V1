DELETE FROM app_users WHERE email = 'endyong18@gmail.com'

SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'app_users'
ORDER BY ordinal_position;

INSERT INTO generations (generation_id, name, year, is_current, created_at, updated_at, app_user_id)
VALUES
    (gen_random_uuid(), 'Generation 1',  '2012-2013', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 2',  '2013-2014', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 3',  '2014-2015', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 4',  '2015-2016', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 5',  '2016-2017', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 6',  '2017-2018', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 7',  '2018-2019', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 8',  '2019-2020', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 9',  '2020-2021', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 10', '2021-2022', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 11', '2022-2023', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 12', '2023-2024', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 13', '2024-2025', false, NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2'),
    (gen_random_uuid(), 'Generation 14', '2025-2026', true,  NOW(), NOW(), 'ab4e70e8-7186-4b09-a9e1-d8b5392855a2');
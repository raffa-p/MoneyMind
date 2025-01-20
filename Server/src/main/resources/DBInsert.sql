/**
 * Author:  Prota Raffaele
 * Created: 13 gen 2025
 */

INSERT INTO user (username, password)
VALUES ('test', '$2a$10$p15oVdXtcG09tZwXhqiry.H/9nceQw1xImzrattfHYR4scEmVMXzO');

INSERT INTO categoria (nome, descrizione)
VALUES
    ('musica', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('cinema', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('casa', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('trasporti', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('salute', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('studio', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('sport', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('svago', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('lavoro', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'),
    ('cibo', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.');


INSERT INTO spesa (categoria, costo, ricorrente, ricorrenza, timestamp, username, prossimopagamento, ricorrenzemese)
VALUES
    ('musica', 29.99, TRUE, '1-mesi', '2024-12-10 08:45:00', 'test', '2024-01-10', 1),
    ('cinema', 15.50, TRUE, '2-mesi', '2024-12-12 14:30:00', 'test', '2024-02-12', 0),
    ('trasporti', 50.00, TRUE, '25-giorni', '2024-12-15 09:00:00', 'test', '2024-01-09', 1),
    ('studio', 100.00, TRUE, '1-mesi', '2024-12-29 13:15:00', 'test', '2024-01-29', 0),
    ('svago', 10.00, TRUE, '1-anni', '2024-12-25 18:00:00', 'test', '2025-12-25', 0),
    ('casa', 200.00, FALSE, NULL, '2024-12-11 10:00:00', 'test', null, 0),
    ('salute', 30.00, FALSE, NULL, '2024-12-13 11:30:00', 'test', null, 0),
    ('lavoro', 150.00, FALSE, NULL, '2024-12-16 16:00:00', 'test', null, 0),
    ('cibo', 50.00, FALSE, NULL, '2024-12-18 08:45:00', 'test', null, 0),
    ('musica', 40.00, FALSE, NULL, '2024-12-22 17:45:00', 'test', null, 0),
    ('cinema', 18.00, FALSE, NULL, '2024-12-23 19:00:00', 'test', null, 0),
    ('trasporti', 60.00, FALSE, NULL, '2024-12-26 08:00:00', 'test', null, 0),
    ('studio', 120.00, FALSE, NULL, '2024-12-28 14:30:00', 'test', null, 0),
    ('musica', 35.00, FALSE, NULL, '2024-12-30 12:30:00', 'test', null, 0),
    ('cinema', 20.00, FALSE, NULL, '2024-12-31 20:15:00', 'test', null, 0),
    ('trasporti', 45.00, FALSE, NULL, '2025-01-02 07:30:00', 'test', null, 0),
    ('studio', 110.00, FALSE, NULL, '2025-01-05 18:00:00', 'test', null, 0),
    ('musica', 33.00, FALSE, NULL, '2025-01-07 14:15:00', 'test', null, 0),
    ('cinema', 22.00, FALSE, NULL, '2025-01-09 09:45:00', 'test', null, 0),
    ('trasporti', 55.00, FALSE, NULL, '2025-01-11 13:00:00', 'test', null, 0),
    ('studio', 125.00, FALSE, NULL, '2025-01-14 16:30:00', 'test', null, 0);


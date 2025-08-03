-- Aziende
INSERT INTO companies ( name, description, email, phone, pIva, created_at) VALUES
('Boolean SRL', 'Azienda di sviluppo software', 'info@boolean.com', '0212345678', 'IT12345678901', NOW()),
('Tech Solutions', 'Servizi IT', 'contact@techsolutions.com', '0298765432', 'IT98765432109', NOW());

-- Tipi di servizio
INSERT INTO service_types ( name) VALUES
('Assistenza'),
('Sviluppo');

-- Servizi
INSERT INTO services ( name, description, status, company_id, service_type_id, created_at) VALUES
('Help Desk', 'Supporto tecnico', 'ACTIVE', 1, 1, NOW()),
('Web Development', 'Sviluppo siti web', 'ACTIVE', 2, 2, NOW());

-- Tipi di ticket
INSERT INTO ticket_types (name, service_id) VALUES
('Bug', 1),
('Richiesta', 2);

-- Ruoli
INSERT INTO roles (name) VALUES
('COMPANY_ADMIN'),
('COMPANY_USER'),
('OPERATOR'),
('ADMIN');



-- Utenti
INSERT INTO users (name, email, password, created_at) VALUES
('Mario Rossi', 'mario.rossi@email.com', 'password1', NOW()),
('Anna Bianchi', 'anna.bianchi@email.com', 'password2', NOW());

-- Associazioni utenti-ruoli
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- Ticket
INSERT INTO tickets (title, description, status, requester_id, assigned_to_id, service_id, type_id, created_at, updated_at)
VALUES
('Problema login', 'Non riesco ad accedere', 'OPEN', 1, 2, 1, 1, NOW(), NOW()),
('Nuova funzionalità', 'Vorrei aggiungere una dashboard', 'OPEN', 2, 1, 2, 2, NOW(), NOW());

-- Storico ticket
INSERT INTO ticket_history (ticket_id, status, changed_by_id, changed_at) VALUES
(1, 'OPEN', 1, NOW()),
(2, 'OPEN', 2, NOW());
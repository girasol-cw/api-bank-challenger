-- Habilita extensões
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabela de endereços
CREATE TABLE IF NOT EXISTS address (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    country VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(100),
    zip_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela accounts
CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nick_name VARCHAR(50),
    dob DATE NOT NULL,
    age INTEGER NOT NULL,
    is_male BOOLEAN NOT NULL,
    document VARCHAR(14) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    account_number UUID NOT NULL DEFAULT uuid_generate_v4(),
    balance DECIMAL(15,2) DEFAULT 0.00,
    full_name VARCHAR(150),
    address_id UUID REFERENCES address(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE -- ADICIONE ESTA LINHA
    );

-- Tabela de transações
CREATE TABLE IF NOT EXISTS transaction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    target_account UUID REFERENCES accounts(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

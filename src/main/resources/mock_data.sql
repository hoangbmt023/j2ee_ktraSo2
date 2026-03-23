-- =============================================
-- Mini E-commerce Database - Schema & Mock Data
-- =============================================

CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Users table (with role: ADMIN / USER)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DOUBLE NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    category_id BIGINT,
    image_url VARCHAR(500),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_price DOUBLE NOT NULL,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Details table
CREATE TABLE IF NOT EXISTS order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =============================================
-- MOCK DATA
-- =============================================

-- Users (password = "123456" hashed with BCrypt)
INSERT INTO users (username, password_hash, fullname, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin System', 'ADMIN'),
('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyen Van A', 'USER');

-- Categories
INSERT INTO categories (name) VALUES
('Điện thoại'),
('Laptop'),
('Phụ kiện');

-- Products
INSERT INTO products (name, price, quantity_in_stock, category_id, image_url) VALUES
('iPhone 15 Pro Max', 34990000, 25, 1, 'https://picsum.photos/seed/iphone15/300/300'),
('Samsung Galaxy S24 Ultra', 31990000, 30, 1, 'https://picsum.photos/seed/samsungs24/300/300'),
('Xiaomi 14 Ultra', 23990000, 20, 1, 'https://picsum.photos/seed/xiaomi14/300/300'),
('OPPO Find X7 Ultra', 22990000, 15, 1, 'https://picsum.photos/seed/oppofindx7/300/300'),
('Google Pixel 8 Pro', 18990000, 18, 1, 'https://picsum.photos/seed/pixel8/300/300'),
('MacBook Pro 16 M3 Max', 89990000, 10, 2, 'https://picsum.photos/seed/macbookpro/300/300'),
('Dell XPS 15', 42990000, 12, 2, 'https://picsum.photos/seed/dellxps15/300/300'),
('ASUS ROG Zephyrus G16', 49990000, 8, 2, 'https://picsum.photos/seed/asusrog/300/300'),
('Lenovo ThinkPad X1 Carbon', 38990000, 14, 2, 'https://picsum.photos/seed/thinkpadx1/300/300'),
('Tai nghe AirPods Pro 2', 6990000, 50, 3, 'https://picsum.photos/seed/airpodspro/300/300'),
('Chuột Logitech MX Master 3S', 2490000, 40, 3, 'https://picsum.photos/seed/mxmaster/300/300'),
('Bàn phím cơ Keychron K8 Pro', 2890000, 35, 3, 'https://picsum.photos/seed/keychron/300/300'),
('Sạc nhanh Anker 65W', 890000, 60, 3, 'https://picsum.photos/seed/anker65w/300/300'),
('Ốp lưng MagSafe iPhone 15', 590000, 100, 3, 'https://picsum.photos/seed/magsafe/300/300');

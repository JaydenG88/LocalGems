-- Cities
INSERT INTO cities (city_id, name, state) VALUES (1, 'San Francisco', 'CA');
INSERT INTO cities (city_id, name, state) VALUES (2, 'New York', 'NY');

-- Categories
INSERT INTO categories (category_id, name) VALUES (1, 'Restaurant');
INSERT INTO categories (category_id, name) VALUES (2, 'Cafe');
INSERT INTO categories (category_id, name) VALUES (3, 'Bar');

-- Businesses
INSERT INTO businesses (business_id, googlePlaceId, name, address, city_id, latitude, longitude, description, dateAdded, website, imageUrl) VALUES
(1, 'gpid1', 'Golden Gate Eats', '123 Market St', 1, 37.7749, -122.4194, 'A great place in SF', '2023-01-01T10:00:00', 'http://goldengateeats.com', 'http://img.com/gge.jpg'),
(2, 'gpid2', 'Central Perk', '456 Broadway', 2, 40.7128, -74.0060, 'Famous coffee shop', '2023-01-02T11:00:00', 'http://centralperk.com', 'http://img.com/cp.jpg');

-- Business Categories (join table)
INSERT INTO business_categories (business_id, category_id) VALUES (1, 1);
INSERT INTO business_categories (business_id, category_id) VALUES (2, 2);

-- Users
INSERT INTO users (user_id, username, email, password, role, date_created) VALUES
(1, 'alice', 'alice@example.com', 'password1', 'USER', '2023-01-01T09:00:00'),
(2, 'bob', 'bob@example.com', 'password2', 'ADMIN', '2023-01-02T09:30:00');

-- Reviews
INSERT INTO reviews (review_id, rating, description, user_id, business_id, created_at, updated_at) VALUES
(1, 5, 'Amazing food!', 1, 1, '2023-01-03T12:00:00', NULL),
(2, 4, 'Great coffee.', 2, 2, '2023-01-04T13:00:00', NULL);

-- Saved Businesses
INSERT INTO saved_businesses (saved_business_id, saved_at, user_id, business_id) VALUES
(1, '2023-01-05T14:00:00', 1, 2),
(2, '2023-01-06T15:00:00', 2, 1);

-- Cities
INSERT INTO cities (city_id, name, state) VALUES 
(1, 'San Francisco', 'CA'),
(2, 'New York', 'NY'),
(3, 'Seattle', 'WA'),
(4, 'Austin', 'TX'),
(5, 'Chicago', 'IL');

-- Categories
INSERT INTO categories (category_id, name) VALUES 
(1, 'Restaurant'),
(2, 'Cafe'),
(3, 'Bar'),
(4, 'Bakery'),
(5, 'Bookstore'),
(6, 'Art Gallery'),
(7, 'Music Venue'),
(8, 'Boutique');

-- Businesses
INSERT INTO businesses (business_id, google_place_id, name, address, city_id, latitude, longitude, description, date_added, website, image_url) VALUES
(1, 'gpid1', 'Golden Gate Eats', '123 Market St', 1, 37.7749, -122.4194, 'A great place in SF', '2023-01-01T10:00:00', 'http://goldengateeats.com', 'http://img.com/gge.jpg'),
(2, 'gpid2', 'Central Perk', '456 Broadway', 2, 40.7128, -74.0060, 'Famous coffee shop', '2023-01-02T11:00:00', 'http://centralperk.com', 'http://img.com/cp.jpg'),
(3, 'gpid3', 'Emerald City Books', '789 Pike St', 3, 47.6062, -122.3321, 'Cozy independent bookstore', '2023-01-03T09:30:00', 'http://emeraldcitybooks.com', 'http://img.com/ecb.jpg'),
(4, 'gpid4', 'Lone Star BBQ', '101 Congress Ave', 4, 30.2672, -97.7431, 'Authentic Texas barbecue', '2023-01-04T12:00:00', 'http://lonestarbbq.com', 'http://img.com/lsbbq.jpg'),
(5, 'gpid5', 'Windy City Jazz', '202 Michigan Ave', 5, 41.8781, -87.6298, 'Historic jazz club', '2023-01-05T20:00:00', 'http://windycityjazz.com', 'http://img.com/wcj.jpg'),
(6, 'gpid6', 'Mission Taqueria', '345 Valencia St', 1, 37.7680, -122.4230, 'Best tacos in the Mission', '2023-01-06T11:30:00', 'http://missiontaqueria.com', 'http://img.com/mt.jpg'),
(7, 'gpid7', 'Brooklyn Bagels', '567 Bedford Ave', 2, 40.7193, -73.9573, 'Authentic NY bagels', '2023-01-07T08:00:00', 'http://brooklynbagels.com', 'http://img.com/bb.jpg');

-- Business Categories (join table)
INSERT INTO business_categories (business_id, category_id) VALUES 
(1, 1),
(2, 2),
(3, 5),
(4, 1),
(5, 3),
(5, 7),
(6, 1),
(7, 4),
(1, 3),
(2, 4);

-- Users
INSERT INTO users (user_id, username, email, password, role, date_created) VALUES
(1, 'alice', 'alice@example.com', 'password1', 'USER', '2023-01-01T09:00:00'),
(2, 'bob', 'bob@example.com', 'password2', 'ADMIN', '2023-01-02T09:30:00'),
(3, 'charlie', 'charlie@example.com', 'password3', 'USER', '2023-01-03T10:15:00'),
(4, 'diana', 'diana@example.com', 'password4', 'USER', '2023-01-04T14:20:00'),
(5, 'evan', 'evan@example.com', 'password5', 'USER', '2023-01-05T16:45:00'),
(6, 'fiona', 'fiona@example.com', 'password6', 'BUSINESS_OWNER', '2023-01-06T11:30:00');

-- Reviews
INSERT INTO reviews (review_id, rating, description, user_id, business_id, created_at, updated_at) VALUES
(1, 5, 'Amazing food!', 1, 1, '2023-01-03T12:00:00', NULL),
(2, 4, 'Great atmosphere, good coffee.', 2, 2, '2023-01-04T15:30:00', NULL),
(3, 5, 'Found some rare books here!', 3, 3, '2023-01-05T11:45:00', NULL),
(4, 3, 'Decent BBQ but a bit pricey.', 4, 4, '2023-01-06T13:20:00', NULL),
(5, 5, 'Best jazz night ever!', 5, 5, '2023-01-07T22:15:00', NULL),
(6, 4, 'Authentic tacos, will come back.', 1, 6, '2023-01-08T12:30:00', NULL),
(7, 5, 'These bagels are the real deal.', 2, 7, '2023-01-09T09:15:00', NULL),
(8, 3, 'Service was slow but food was good.', 3, 1, '2023-01-10T14:00:00', NULL),
(9, 4, 'Coffee is excellent!', 4, 2, '2023-01-11T10:45:00', NULL),
(10, 5, 'The live music was incredible!', 5, 5, '2023-01-12T21:30:00', NULL);

-- Saved Businesses
INSERT INTO saved_businesses (saved_business_id, user_id, business_id, saved_at) VALUES
(1, 1, 1, '2023-01-03T13:00:00'),
(2, 1, 6, '2023-01-08T13:30:00'),
(3, 2, 2, '2023-01-04T16:00:00'),
(4, 2, 7, '2023-01-09T10:00:00'),
(5, 3, 3, '2023-01-05T12:30:00'),
(6, 4, 4, '2023-01-06T14:00:00'),
(7, 5, 5, '2023-01-07T23:00:00'),
(8, 3, 1, '2023-01-10T15:00:00'),
(9, 4, 2, '2023-01-11T11:30:00'),
(10, 5, 6, '2023-01-12T18:00:00');

-- Reset the sequence so new inserts get the correct next ID
SELECT setval('businesses_business_id_seq', (SELECT MAX(business_id) FROM businesses));
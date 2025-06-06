CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    language VARCHAR(100),
    director VARCHAR(255)
);

INSERT INTO movies (title, genre, language, director) VALUES
('Inception', 'Sci-Fi', 'English', 'Christopher Nolan'),
('Parasite', 'Thriller', 'Korean', 'Bong Joon-ho'),
('Interstellar', 'Sci-Fi', 'English', 'Christopher Nolan'),
('The Dark Knight', 'Action', 'English', 'Christopher Nolan'),
('Spirited Away', 'Animation', 'Japanese', 'Hayao Miyazaki');

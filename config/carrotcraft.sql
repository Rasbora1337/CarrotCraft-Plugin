-- phpMyAdmin SQL Dump
-- version 4.9.7deb1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 10, 2021 at 11:10 PM
-- Server version: 8.0.27-0ubuntu0.21.04.1
-- PHP Version: 7.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `carrotcraft`
--

-- --------------------------------------------------------

--
-- Table structure for table `arrowEffects`
--

CREATE TABLE `arrowEffects` (
  `ID` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `color` varchar(255) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `material` varchar(255) DEFAULT NULL,
  `particle` varchar(255) NOT NULL,
  `count` int NOT NULL DEFAULT '1',
  `speed` int NOT NULL DEFAULT '0',
  `trailMaterial` varchar(255) DEFAULT NULL,
  `removeDelay` int NOT NULL DEFAULT '20',
  `spawnDelay` int NOT NULL DEFAULT '200',
  `rank` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `glowEffects`
--

CREATE TABLE `glowEffects` (
  `ID` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `color` varchar(255) NOT NULL,
  `material` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `glow` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rank` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `ID` int NOT NULL,
  `user` varchar(255) NOT NULL,
  `email` text NOT NULL,
  `rank` int NOT NULL,
  `amount` int NOT NULL,
  `paymentID` text NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `state`
--

CREATE TABLE `state` (
  `ID` int NOT NULL,
  `user` varchar(255) NOT NULL,
  `arrowEffect` int DEFAULT NULL,
  `glowing` int DEFAULT NULL,
  `trailEffect` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stats`
--

CREATE TABLE `stats` (
  `ID` int NOT NULL,
  `user` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `carrots` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `trailEffects`
--

CREATE TABLE `trailEffects` (
  `ID` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `color` varchar(255) NOT NULL,
  `material` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `particle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `count` int NOT NULL DEFAULT '1',
  `speed` int NOT NULL DEFAULT '0',
  `trailMaterial` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `removeDelay` int NOT NULL DEFAULT '20',
  `spawnDelay` int NOT NULL DEFAULT '200',
  `rank` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `userArrowEffects`
--

CREATE TABLE `userArrowEffects` (
  `ID` int NOT NULL,
  `user` int NOT NULL,
  `effect` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `userGlowEffects`
--

CREATE TABLE `userGlowEffects` (
  `ID` int NOT NULL,
  `user` int NOT NULL,
  `effect` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `ID` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `discordID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `registerDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `donor` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `userTrailEffects`
--

CREATE TABLE `userTrailEffects` (
  `ID` int NOT NULL,
  `user` int NOT NULL,
  `effect` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `arrowEffects`
--
ALTER TABLE `arrowEffects`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `glowEffects`
--
ALTER TABLE `glowEffects`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `state`
--
ALTER TABLE `state`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `user` (`user`),
  ADD KEY `arrowEffect` (`arrowEffect`),
  ADD KEY `glowing` (`glowing`),
  ADD KEY `trailEffect` (`trailEffect`);

--
-- Indexes for table `stats`
--
ALTER TABLE `stats`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `user` (`user`(255));

--
-- Indexes for table `trailEffects`
--
ALTER TABLE `trailEffects`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `userArrowEffects`
--
ALTER TABLE `userArrowEffects`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `user` (`user`),
  ADD KEY `effect` (`effect`);

--
-- Indexes for table `userGlowEffects`
--
ALTER TABLE `userGlowEffects`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `user` (`user`),
  ADD KEY `effect` (`effect`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `name` (`name`),
  ADD UNIQUE KEY `discordID` (`discordID`);

--
-- Indexes for table `userTrailEffects`
--
ALTER TABLE `userTrailEffects`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `user` (`user`),
  ADD KEY `effect` (`effect`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `arrowEffects`
--
ALTER TABLE `arrowEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `glowEffects`
--
ALTER TABLE `glowEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `state`
--
ALTER TABLE `state`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stats`
--
ALTER TABLE `stats`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `trailEffects`
--
ALTER TABLE `trailEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userArrowEffects`
--
ALTER TABLE `userArrowEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userGlowEffects`
--
ALTER TABLE `userGlowEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userTrailEffects`
--
ALTER TABLE `userTrailEffects`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `state`
--
ALTER TABLE `state`
  ADD CONSTRAINT `state_ibfk_2` FOREIGN KEY (`glowing`) REFERENCES `glowEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `state_ibfk_4` FOREIGN KEY (`arrowEffect`) REFERENCES `arrowEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `state_ibfk_5` FOREIGN KEY (`trailEffect`) REFERENCES `trailEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `state_ibfk_6` FOREIGN KEY (`user`) REFERENCES `users` (`name`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `userArrowEffects`
--
ALTER TABLE `userArrowEffects`
  ADD CONSTRAINT `userArrowEffects_ibfk_1` FOREIGN KEY (`user`) REFERENCES `users` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `userArrowEffects_ibfk_2` FOREIGN KEY (`effect`) REFERENCES `arrowEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `userGlowEffects`
--
ALTER TABLE `userGlowEffects`
  ADD CONSTRAINT `userGlowEffects_ibfk_1` FOREIGN KEY (`user`) REFERENCES `users` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `userGlowEffects_ibfk_2` FOREIGN KEY (`effect`) REFERENCES `glowEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `userTrailEffects`
--
ALTER TABLE `userTrailEffects`
  ADD CONSTRAINT `userTrailEffects_ibfk_1` FOREIGN KEY (`user`) REFERENCES `users` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `userTrailEffects_ibfk_2` FOREIGN KEY (`effect`) REFERENCES `trailEffects` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2.1
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 30-05-2019 a las 14:20:19
-- Versión del servidor: 5.7.25-0ubuntu0.16.04.2
-- Versión de PHP: 7.0.33-1+ubuntu16.04.1+deb.sury.org+1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `BDSegura`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `DOMICILIO`
--

CREATE TABLE `DOMICILIO` (
  `ID` int(6) NOT NULL,
  `DIRECCION` varchar(400) DEFAULT NULL,
  `UUIDUNIQUE` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `FOTOS`
--

CREATE TABLE `FOTOS` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(50) NOT NULL,
  `URL` varchar(400) NOT NULL,
  `PATH` varchar(400) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `GALERIA`
--

CREATE TABLE `GALERIA` (
  `IDUSUARIO` varchar(50) NOT NULL,
  `PATH` varchar(300) NOT NULL,
  `UUIDUNIQUE` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TELEFONO`
--

CREATE TABLE `TELEFONO` (
  `ID` int(6) NOT NULL,
  `NUMERO` varchar(35) DEFAULT NULL,
  `UUIDUNIQUE` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `USUARIOS`
--

CREATE TABLE `USUARIOS` (
  `ID` varchar(20) NOT NULL,
  `FOTO` varchar(200) DEFAULT NULL,
  `NOMBRE` varchar(100) DEFAULT NULL,
  `APELLIDOS` varchar(100) DEFAULT NULL,
  `GALERIAID` int(6) DEFAULT NULL,
  `DOMICILIOID` int(6) DEFAULT NULL,
  `TELEFONOID` int(6) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `UUIDUNIQUE` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `USUARIOS_GALERIA`
--

CREATE TABLE `USUARIOS_GALERIA` (
  `NOMBRE` varchar(50) DEFAULT NULL,
  `EMAIL` varchar(50) NOT NULL,
  `FOTO` varchar(200) DEFAULT NULL,
  `UUIDUNIQUE` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `FOTOS`
--
ALTER TABLE `FOTOS`
  ADD PRIMARY KEY (`ID`);

--
-- Indices de la tabla `GALERIA`
--
ALTER TABLE `GALERIA`
  ADD PRIMARY KEY (`IDUSUARIO`,`PATH`);

--
-- Indices de la tabla `USUARIOS`
--
ALTER TABLE `USUARIOS`
  ADD PRIMARY KEY (`ID`,`UUIDUNIQUE`);

--
-- Indices de la tabla `USUARIOS_GALERIA`
--
ALTER TABLE `USUARIOS_GALERIA`
  ADD PRIMARY KEY (`EMAIL`,`UUIDUNIQUE`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `FOTOS`
--
ALTER TABLE `FOTOS`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- sudo docker pull erkules/galera:basic
-- sudo docker run --detach=true --name node1 -h node1 erkules/galera:basic --wsrep-cluster-name=local-test --wsrep-cluster-address=gcomm://
-- sudo docker run --detach=true --name node2 -h node2 --link node1:node1 erkules/galera:basic --wsrep-cluster-name=local-test --wsrep-cluster-address=gcomm://node1
-- sudo docker run --detach=true --name node3 -h node3 --link node1:node1 erkules/galera:basic --wsrep-cluster-name=local-test --wsrep-cluster-address=gcomm://node1

-- sudo docker exec -ti node1 mysql -e 'show status like "wsrep_cluster_size"'
-- docker ps -a
-- sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' 72e25af87cf9

-- CREATE DATABASE `test` /*!40100 DEFAULT CHARACTER SET utf8 */;


-- drop user test;
-- CREATE USER 'test'@'%' IDENTIFIED BY 'test';
-- GRANT ALL PRIVILEGES ON test TO 'test';

DROP TABLE IF EXISTS `counter`;
CREATE TABLE `counter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `counter` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into counter(counter) values (0);
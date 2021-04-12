DROP TABLE IF EXISTS upgrade;
DROP TABLE IF EXISTS cluster;
DROP TABLE IF EXISTS cluster_state;

CREATE TABLE upgrade (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  upgrade_name VARCHAR(250) NOT NULL,
  version VARCHAR(250) NOT NULL,
  rollout_strategy VARCHAR(250) NOT NULL
);

CREATE TABLE cluster (
   id INT AUTO_INCREMENT  PRIMARY KEY,
   cluster_id INT NOT NULL,
   version VARCHAR(250) NOT NULL,
   status VARCHAR(250) NOT NULL
);

CREATE TABLE cluster_state (
  current_upgrade_id INT
);


DROP TABLE IF EXISTS upgrade;
DROP TABLE IF EXISTS cluster;
DROP TABLE IF EXISTS cluster_state;

CREATE TABLE upgrade (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  upgrade_name VARCHAR(250) NOT NULL,
  summary VARCHAR(250) NULL,
  version VARCHAR(250) NOT NULL,
  patch_uri VARCHAR(250) NULL,
  patch_type VARCHAR(250) NOT NULL
);

CREATE TABLE cluster (
   id INT AUTO_INCREMENT  PRIMARY KEY,
   cluster_id INT NOT NULL,
   version VARCHAR(250) NOT NULL,
   status VARCHAR(250) NOT NULL
);

CREATE TABLE cluster_state (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  current_upgrade_id INT,
  rollout_strategy VARCHAR(250) NOT NULL
);


export DROPSI_WEB_PORT=8922
export DROPSI_DATASOURCE_USERNAME=sw_mathias_schoettle
export DROPSI_DATASOURCE_PASSWORD=scm30415
export DROPSI_DATASOURCE_URL=jdbc:mysql://im-knuth.oth-regensburg.de:3306/sw_mathias_schoettle
java -jar ./dropsi-0.0.1.jar > server.log 2> error.log &

package com.cyberisk.platform.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.jdbc.PGDataSource;
import com.mobility.demo.controller.CarController;
import com.mobility.demo.controller.UserController;
import com.mobility.demo.model.Car;
import com.mobility.demo.model.User;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Statement;

@Component
@Log
@Data
@DependsOn()
public class PgNotification implements InitializingBean, DisposableBean {

    private final PGConnection pgConnection;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CarController carController;
    private final UserController userController;

    public PgNotification(PGDataSource dataSource, CarController carController, UserController userController) throws Throwable, Exception {
        pgConnection = (PGConnection) dataSource.getConnection();
        this.carController = carController;
        this.userController = userController;
        pgConnection.addNotificationListener((processId, channelName, payload) -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(payload);
                JsonNode data = jsonNode.get("data");
                switch (jsonNode.get("table").asText()) {
                    case "car":
                        this.carController.handleCar(
                                new Car(
                                        data.get("id").toString(),
                                        data.get("x").asDouble(),
                                        data.get("y").asDouble()
                                )
                        );
                    case "users":
                        this.userController.handleUser(
                                new User(
                                        data.get("id").toString(),
                                        data.get("x").asDouble(),
                                        data.get("y").asDouble()
                                )
                        );
                }
            } catch (IOException e) {
                log.severe("Problem while reading payload");
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        Statement statement = pgConnection.createStatement();
        statement.execute("UNLISTEN events");
        statement.close();
        pgConnection.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String sql = "CREATE TRIGGER users_notify_event\n" +
                "  AFTER INSERT OR UPDATE OR DELETE ON users\n" +
                "  FOR EACH ROW EXECUTE PROCEDURE notify_event();\n" +
                "\n" +
                "CREATE TRIGGER car_notify_event\n" +
                "  AFTER INSERT OR UPDATE OR DELETE ON car\n" +
                "  FOR EACH ROW EXECUTE PROCEDURE notify_event();";
        pgConnection.setAutoCommit(true);
        Statement st = pgConnection.createStatement();
        st.executeUpdate(sql);
        st.close();

        Statement statement = pgConnection.createStatement();
        statement.execute("LISTEN events");
        statement.close();
    }
}

import com.tuspring.config.ApplicationConfig;
import com.tuspring.config.DatabaseSetup;
import com.tuspring.file.FileConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Please provide the path to the configuration file.");
        }

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        DatabaseSetup databaseSetup = context.getBean(DatabaseSetup.class);

        logger.info(String.format("Entry file name is: %s", args[0]));
        FileConfigurator config = new FileConfigurator(args[0]);

        var createdTables = databaseSetup.createTables(config);

        logger.info("------- Tables with populated Data -------");
        createdTables.forEach(logger::info);

        logger.info(String.format("Data from table: %s", createdTables.get(0)));
        databaseSetup.showTableData(createdTables.get(0));

        databaseSetup.dropTables(createdTables);
        logger.info("------- Tables dropped -------");

        context.close();
    }
}

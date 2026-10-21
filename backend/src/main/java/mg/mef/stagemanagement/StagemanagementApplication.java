package mg.mef.stagemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class StagemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StagemanagementApplication.class, args);
	}

}

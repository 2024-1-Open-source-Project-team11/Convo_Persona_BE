package OSS_group11.ConvoPersona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConvoPersonaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvoPersonaApplication.class, args);
    }

}

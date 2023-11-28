package de.samply.spring;

import de.samply.utils.ProjectVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MtbaController {

    private final String projectVersion = ProjectVersion.getProjectVersion();

    @GetMapping(value = MtbaConst.INFO)
    public ResponseEntity<String> info() {
        return new ResponseEntity<>(projectVersion, HttpStatus.OK);
    }

}

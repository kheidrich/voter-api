package br.edu.ulbra.election.voter.api.v1;

import br.edu.ulbra.election.voter.input.v1.LoginInput;
import br.edu.ulbra.election.voter.input.v1.TokenValidationInput;
import br.edu.ulbra.election.voter.output.v1.LoginOutput;
import br.edu.ulbra.election.voter.output.v1.TokenValidationOutput;
import br.edu.ulbra.election.voter.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;

    @Autowired
    public AuthApi(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginOutput login(@RequestBody LoginInput loginInput){
        return this.authService.login(loginInput);
    }

    @PostMapping("/token/validate")
    public TokenValidationOutput validateToken(@RequestBody TokenValidationInput tokenValidationInput){
        return this.authService.validateToken(tokenValidationInput);
    }
}

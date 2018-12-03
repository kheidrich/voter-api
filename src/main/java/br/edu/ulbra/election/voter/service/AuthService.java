package br.edu.ulbra.election.voter.service;

import br.edu.ulbra.election.voter.exception.GenericOutputException;
import br.edu.ulbra.election.voter.input.v1.LoginInput;
import br.edu.ulbra.election.voter.input.v1.TokenValidationInput;
import br.edu.ulbra.election.voter.model.Token;
import br.edu.ulbra.election.voter.model.Voter;
import br.edu.ulbra.election.voter.output.v1.LoginOutput;
import br.edu.ulbra.election.voter.output.v1.TokenValidationOutput;
import br.edu.ulbra.election.voter.repository.TokenRepository;
import br.edu.ulbra.election.voter.repository.VoterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final VoterRepository voterRepository;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, TokenRepository tokenRepository, VoterRepository voterRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.voterRepository = voterRepository;
    }

    public LoginOutput login(LoginInput loginInput) {
        if (loginInput.getEmail() == null)
            throw new GenericOutputException("Email not given");

        if (loginInput.getPassword() == null)
            throw new GenericOutputException("Password not given");

        Voter voter = voterRepository.findFirstByEmail(loginInput.getEmail());
        if (!passwordEncoder.matches(loginInput.getPassword(), voter.getPassword()))
            throw new GenericOutputException("Invalid password");

        Token token = tokenRepository.findFirstByVoter(voter);
        if (token == null) {
            token = new Token();
            token.setVoter(voter);
        }

        token.setExpireDate(calculateExpiryDate());
        token.setToken(generateToken(token));
        tokenRepository.save(token);
        LoginOutput loginOutput = new LoginOutput();
        loginOutput.setToken(token.getToken());

        return loginOutput;
    }

    public TokenValidationOutput validateToken(TokenValidationInput tokenValidationInput) {
        TokenValidationOutput tokenValidationOutput = new TokenValidationOutput();
        boolean noTokenProvided, invalidToken, tokenExpired, belongsToVoter;

        Token token = tokenRepository.findFirstByToken(tokenValidationInput.getToken());
        noTokenProvided = tokenValidationInput.getToken() == null || tokenValidationInput.getToken().isEmpty();
        invalidToken = token == null;

        if(invalidToken || noTokenProvided)
            throw new GenericOutputException("Invalid token");

        tokenExpired = (new Date()).after(token.getExpireDate());
        belongsToVoter = tokenValidationInput.getVoterId() != null && tokenValidationInput.getVoterId().equals(token.getVoter().getId());
        tokenValidationOutput.setValid(!tokenExpired && belongsToVoter);

        return tokenValidationOutput;
    }

    private Date calculateExpiryDate() {
        long TOKEN_DURATION = 60000L;

        return new Date(Instant.now().toEpochMilli() + TOKEN_DURATION);
    }

    private String generateToken(Token token) {
        return passwordEncoder.encode(token.getVoter().getId() + token.getExpireDate().toString());
    }
}

package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.auth.LoginRequest;
import mg.mef.stagemanagement.dto.auth.LoginResponse;
import mg.mef.stagemanagement.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController 
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping ("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

   @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me(@AuthenticationPrincipal AuthMeResponse utilisateur) {
        return ResponseEntity.ok(utilisateur);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout( @RequestHeader(value = "Authorization",required=false) String authorizationHeader){
        String token = extraireToken(authorizationHeader);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    private String extraireToken(String authorizationHeader){
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Token manquant"
            );
        }

        return authorizationHeader.substring(7);
    }

}

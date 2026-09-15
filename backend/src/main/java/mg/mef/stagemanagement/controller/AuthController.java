package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.auth.LoginRequest;
import mg.mef.stagemanagement.dto.auth.LoginResponse;
import mg.mef.stagemanagement.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

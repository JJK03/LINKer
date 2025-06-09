package project.Network;
// LoginListener.java
public interface LoginListener {
    void onLoginSuccess(String userId);
    void onLoginFailure(String reason);
    void onSignUpSuccess(String userId);
    void onSignUpFailure(String reason);
}

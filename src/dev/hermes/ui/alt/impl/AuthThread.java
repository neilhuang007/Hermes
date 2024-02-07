package dev.hermes.ui.alt.impl;


import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.hermes.ui.alt.account.Account;
import dev.hermes.ui.alt.account.MicrosoftLogin;
import dev.hermes.utils.SkinUtil;
import dev.hermes.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;



public class AuthThread extends Thread {

    TimerUtil TimerUtil = new TimerUtil();
    private final String password;
    private String status;
    private final String username;

    public Account account;

    private final String refreshtoken;

    private final String accounttype;
    private Minecraft mc = Minecraft.getMinecraft();

    TimerUtil watch = new TimerUtil();

    public AuthThread(String username, String password, String refreshtoken, String accounttype) {
        super("Alt Login Thread");
        this.accounttype = accounttype;
        if(accounttype.equals("CRACKED")){
            this.account = new Account(username,password, SkinUtil.uuidOf("Steve"),refreshtoken,accounttype);
        }else{
            this.account = new Account(username,password, SkinUtil.uuidOf(username),refreshtoken,accounttype);
        }
        this.username = username;
        this.password = password;
        this.refreshtoken = refreshtoken;
        status = (ChatFormatting.GRAY + "Waiting...");
    }

    public Account getAccount() {
        return account;
    }

//    public AuthThread(String username, String password, String accounttype) {
//        super("Alt Login Thread");
//        this.username = username;
//        this.password = password;
//        this.refreshtoken = "";
//        this.accounttype = accounttype;
//        status = (ChatFormatting.GRAY + "Waiting...");
//    }


    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();

            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
        }
        return null;
    }

    public String getStatus() {
        return status;
    }

    private MicrosoftLogin.LoginData loginWithRefreshToken(String refreshToken) {
        final MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        mc.session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft");
        return loginData;
    }

    @Override
    public void run() {
        if(accounttype.equals("CRACKED")){
            mc.session = new Session(username, "", "", "mojang");
            setStatus(ChatFormatting.GREEN + "Logged in. (" + username + " - offline name)");
            return;
        } else if (accounttype.equals("MOJANG")) {
//            Hermes.switchToMojang();

            setStatus(ChatFormatting.AQUA + "Logging in...");
            Session auth = createSession(username, password);
            if (auth == null) {
                setStatus(ChatFormatting.RED + "Login failed!");
            } else {
                setStatus((ChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")"));
                mc.session = auth;
            }
        } else{
            Account account = getAccount();
            String refreshToken = refreshtoken;
            if (refreshtoken!= null) {
                new Thread(() -> {
                    status = (ChatFormatting.YELLOW + "Logging in as " + account.getUsername() + "" + " do not hit back");
                    MicrosoftLogin.LoginData loginData = loginWithRefreshToken(refreshToken);
                    account.setUsername(loginData.username);
                    account.setRefreshToken(loginData.newRefreshToken);
                    if(account.getUsername() == null){
                        setStatus(ChatFormatting.RED + "Login Failed, please retry");
                    }
                    else{
                        setStatus(ChatFormatting.GREEN + "Succesfully logged in as " + loginData.username);
                    }
                }).start();
            }
            else {

                setStatus(ChatFormatting.RED + "Login failed!");
            }


        }

    }

    public void setStatus(String status) {
        watch.resetTimer();
        this.status = status;
        if(watch.finished(5000)){
            status = ChatFormatting.GRAY + "Idle...";
        }
    }
}

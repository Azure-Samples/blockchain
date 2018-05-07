/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package standalonedriver;

import com.microsoft.aad.adal4j.AuthenticationCallback;
import io.swagger.client.*;
import io.swagger.client.api.UsersApi;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import io.swagger.client.auth.OAuth;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jdk.nashorn.internal.ir.RuntimeNode;

/**
 *
 * @author chmar
 */
public class StandAloneDriver {

    private static AuthenticationResult mToken;
    
    private static String authority = "https://login.windows.net/[your tenant]/oauth2/authorize";
    private static String key = "[your sp secret]";
    private static String client = "[your sp id]";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
           
           
           
            //ADAL4j
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationContext ctx = new AuthenticationContext(authority, false, service);
            ClientCredential clientCred = new ClientCredential(client, key);
            AuthenticationResult authResult = ctx.acquireToken(client, 
                   clientCred,null).get();
            
            //API
            UsersApi api = new UsersApi();
            //api.getApiClient().setBasePath("http://localhost:3031"); use for custom adress
            api.getApiClient().addDefaultHeader("Authorization", "Bearer "+ authResult.getAccessToken());
            System.out.println(api.meGet());
            
            
           
        } catch (Exception ex) {
            Logger.getLogger(StandAloneDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static AuthenticationResult getLocalToken() {
        return mToken;
    }

    private static void setLocalToken(AuthenticationResult newToken) {
        mToken = newToken;
    }
}

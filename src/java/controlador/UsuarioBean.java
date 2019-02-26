package controlador;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;

@Named
@SessionScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 3658300628580536494L;

    private SocialAuthManager socialManager;
    private Profile profile;

    private final String mainURL = "http://localhost:8085/inicio_sesion_redes/faces/index.xhtml";
    private final String redirectURL = "http://localhost:8085/inicio_sesion_redes/faces/redirectHome.xhtml";
    private final String provider = "facebook";

    public void conectar() throws Exception {
        Properties prop = System.getProperties();
        prop.put("graph.facebook.com.consumer_key", "");
        prop.put("graph.facebook.com.consumer_secret", "");
        prop.put("graph.facebook.com.custom_permissions", "public_profile,email");

        SocialAuthConfig socialConfig = SocialAuthConfig.getDefault();
        try {
            socialConfig.load(prop);
            socialManager = new SocialAuthManager();
            socialManager.setSocialAuthConfig(socialConfig);
            String URLRetorno = socialManager.getAuthenticationUrl(provider, redirectURL);
            FacesContext.getCurrentInstance().getExternalContext().redirect(URLRetorno);
        } catch (IOException e) {
            System.out.println("Error en el social\n"+e);
        }
    }

    public void getPerfilUsuario() throws Exception {
        try {
            System.out.println("INGRESO AL METODO PARA RECUPERAR EL PERFIL DEL USUARIO");
            ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ex.getRequest();
            Map<String, String> parametros = SocialAuthUtil.getRequestParametersMap(request);

            if (socialManager != null) {
                AuthProvider prov = socialManager.connect(parametros);
                this.setProfile(prov.getUserProfile());
            } else {
                System.out.println("ESTA VACIO EL SOCIALMANAGER");
            }
            
            FacesContext.getCurrentInstance().getExternalContext().redirect(mainURL);
            
        } catch (IOException e) {
            System.out.println("SALTO A UNA EXCEPCION"+e);
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}

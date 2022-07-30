package de.galaxy102.jitsi.jwt;

import io.smallrye.jwt.build.Jwt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URL;
import java.time.Duration;

/**
 * Service to create JWT conforming Jitsi's specification
 *
 * @author Konstantin Köhring &lt;konsti@galaxy102.de&gt;
 */
@ApplicationScoped
public class JitsiJwtProvider {

    @Inject
    protected JitsiJwtProviderConfig jwtConfig;

    private static String calculateSubject(URL jitsiBaseUrl) {
        return jitsiBaseUrl.getHost();
    }

    /**
     * Build a Jitsi JWT
     *
     * @param roomId    Room ID to authorize
     * @param userData  User Metadata
     * @return Signed JWT to send to Jitsi
     */
    public String buildToken(@NotBlank String roomId, @Valid JitsiJwtUserData userData) {
        // Claims see https://github.com/jitsi/lib-jitsi-meet/blob/master/doc/tokens.md#token-structure
        return Jwt
                .issuer(this.jwtConfig.appId())
                .expiresIn(Duration.ofSeconds(this.jwtConfig.validity()))
                .subject(calculateSubject(this.jwtConfig.jitsiUrl()))
                .audience(this.jwtConfig.appId())
                .claim(JitsiJwtClaims.ROOM, roomId)
                .claim(JitsiJwtClaims.CONTEXT, userData.forJwt())
                .signWithSecret(this.jwtConfig.secret());
    }
}

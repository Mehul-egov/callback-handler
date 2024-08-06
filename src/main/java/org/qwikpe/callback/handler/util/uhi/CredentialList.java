package org.qwikpe.callback.handler.util.uhi;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.qwikpe.callback.handler.domain.b2b.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.repository.b2b.uhi.CredentialsInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class CredentialList {

    @Autowired
    private CredentialsInfoRepository credentialsInfoRepository;

    private static List<CredentialsInfo> credentialsInfoList;

    @PostConstruct
    public void getCredentialsInfo() {
        credentialsInfoList = credentialsInfoRepository.findAll();
    }

    public CredentialsInfo getCredentialsInfo(String domain, String subscriberId) {

        return credentialsInfoList.stream().filter(
                obj -> (obj.getDomain().equals(domain) && obj.getSubscriberId().equals(subscriberId))
        ).findFirst().orElse(null);
    }
}

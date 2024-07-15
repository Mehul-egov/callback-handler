package org.qwikpe.callback.handler.repository.uhi;

import org.qwikpe.callback.handler.domain.uhi.CredentialsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsInfoRepository extends JpaRepository<CredentialsInfo, Long> {
    public CredentialsInfo findByDomainAndSubscriberId(String domain, String subscriberId);
}

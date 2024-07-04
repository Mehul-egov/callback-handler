package org.qwikpe.callback.handler.repositories.uhi;

import org.qwikpe.callback.handler.entities.uhi.CredentialsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsInfoRepository extends JpaRepository<CredentialsInfo, Long> {
    public CredentialsInfo findByDomainAndSubscriberId(String domain, String subscriberId);
}

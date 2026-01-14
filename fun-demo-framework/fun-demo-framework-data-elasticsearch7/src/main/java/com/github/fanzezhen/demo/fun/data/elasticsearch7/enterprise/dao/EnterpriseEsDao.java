package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.dao;

import com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity.EnterpriseDocument;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.dao.AbstractEsDao;
import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class EnterpriseEsDao extends AbstractEsDao<EnterpriseDocument> {

    public Class<EnterpriseDocument> getDocumentClass(){
        return EnterpriseDocument.class;
    }

}

package com.yonyou.nc.codevalidator.persistence.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.yonyou.nc.codevalidator.persistence.dao.RuleDaoException;

/**
 * �ڹ���JPAִ�л����µ�ʵ�������
 * 
 * @author mazhqa
 * @since V2.7
 */
public class RuleHibernateJPAEntityManager {

	public static final String DEFAULT_PERSISTENCE_NAME = "rule-hibernate";

	private static EntityManagerFactory entityManagerFactory;

	private static RuleHibernateJPAEntityManager instance;

	public static RuleHibernateJPAEntityManager getInstance() {
		if (instance == null) {
			synchronized (RuleHibernateJPAEntityManager.class) {
				if (instance == null) {
					instance = new RuleHibernateJPAEntityManager();
				}
			}
		}
		return instance;
	}

	private RuleHibernateJPAEntityManager() {
		Map<String, String> optionMap = new HashMap<String, String>();
//		optionMap.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)"); //$NON-NLS-1$ //$NON-NLS-2$
//		optionMap.put("openjpa.Log", "DefaultLevel=WARN, Runtime=WARN, Tool=WARN");
		optionMap.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
//		optionMap.put("openjpa.ConnectionFactoryProperties", "PrintParameters=True");
//		optionMap.put("openjpa.DetachState", "loaded");
//		optionMap.put("openjpa.ConnectionURL", "jdbc:postgresql://20.1.83.57:5432/ncrcc");
		entityManagerFactory = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_NAME);
	}

	/**
	 * ����һ���µ�ʵ�������
	 * @return - ��ǰ��û��EJB�����Զ���EntityManager����ȫ�������ڵĹ�������Ҫ�ֶ��ͷ�
	 * @throws RuleDaoException - �������ಢû�г�ʼ���ɹ��������ڲ����ܻ�ȡ��Ӧ��ʵ�������
	 */
	public EntityManager createEntityManager() throws RuleDaoException {
		if (entityManagerFactory == null) {
			throw new RuleDaoException("JPA Entity Manage�Ĺ�����δ�����ɹ�!");
		}
		return entityManagerFactory.createEntityManager();
	}

}
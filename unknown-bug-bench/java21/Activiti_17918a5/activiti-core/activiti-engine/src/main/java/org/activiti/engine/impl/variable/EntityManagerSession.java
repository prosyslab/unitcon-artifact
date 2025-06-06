/*
 * Copyright 2010-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.activiti.engine.impl.variable;

// import jakarta.persistence.EntityManager;
// import jakarta.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.api.internal.Internal;
import org.activiti.engine.impl.interceptor.Session;

/**

 */
@Internal
public interface EntityManagerSession extends Session {
  /**
   * Get an {@link EntityManager} instance associated with this session.
   *
   * @throws ActivitiException
   *           when no {@link EntityManagerFactory} instance is configured for the process engine.
   */
  EntityManager getEntityManager();
}

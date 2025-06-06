/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.engine.impl.event;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.bpmn.behavior.BoundaryEventActivityBehavior;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * @author Daniel Meyer
 */
public abstract class AbstractEventHandler implements EventHandler {

  private static Logger log = Logger.getLogger(AbstractEventHandler.class.getName());
  
  public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {

    ExecutionEntity execution = eventSubscription.getExecution();
    ActivityImpl activity = eventSubscription.getActivity();
    ActivityImpl activityOfExecution = execution.getActivity();

    if (activity == null) {
      throw new ActivitiException("Error while sending signal for event subscription '" + eventSubscription.getId() + "': "
              + "no activity associated with event subscription");
    }

    if (!activityOfExecution.equals(activity)) { // Unitcon modified here: split code for exact stack trace comparison
      execution.setActivity(activity);
    }

    if (payload instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> processVariables = (Map<String, Object>) payload;
      execution.setVariables(processVariables);
    }

    if (activity.getActivityBehavior() instanceof BoundaryEventActivityBehavior) {

      try {

        activity
          .getActivityBehavior()
          .execute(execution);

      } catch (RuntimeException e) {
        log.log(Level.SEVERE, "exception while sending signal for event subscription '" + eventSubscription + "'", e);
        throw e;

      } catch (Exception e) {
        log.log(Level.SEVERE, "exception while sending signal for event subscription '" + eventSubscription + "'", e);
        throw new ActivitiException("exception while sending signal for event subscription '" + eventSubscription + "':" + e.getMessage(), e);
      }

    } else { // not boundary
      execution.signal("signal", null);
    }

  }
}

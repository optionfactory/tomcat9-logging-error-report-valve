package net.optionfactory.tomcat9.asl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

public class AbortOnDeploymentFailure implements LifecycleListener {

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        final Lifecycle lifecycle = event.getLifecycle();
        if (lifecycle instanceof Server == false || !event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
            return;
        }
        final Server server = (Server) lifecycle;
        final List<String> failures = containers(server).stream()
                .filter(c -> c.getState() != LifecycleState.STARTED)
                .map(c -> String.format("[%s][%s] %s", c.getClass().getSimpleName(), c.getName(), c.getStateName()))
                .collect(Collectors.toList());

        if (failures.isEmpty()) {
            return;
        }
        throw new IllegalStateException(failures.toString());
    }

    private List<Container> containers(Server server) {
        final List<Container> containers = new ArrayList<>();
        for (Service service : server.findServices()) {
            accumulateContainers(service.getContainer(), containers);
        }
        return containers;
    }

    private void accumulateContainers(Container container, List<Container> containers) {
        containers.add(container);
        for (Container child : container.findChildren()) {
            accumulateContainers(child, containers);
        }
    }

}

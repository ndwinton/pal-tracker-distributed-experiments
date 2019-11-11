### Adding C2C support

There are two things that need to be done to add C2C support for
service discovery.
The first is to ensure that Eureka registration uses internal IP
addresses and ports rather than GoRouter routes.
This can be done by adding an `application-cloud.properties` file
to the registration server (which is the one that matters).
This file will only be used when the `cloud` profile is active,
which is the case when running under PAS.

The properties added by the file are as follows:

* `eureka.instance.prefer-ip-address=true` -- This ensures that
  IP addresses are registered instead of DNS names, so the
  GoRouter is not used.
* `eureka.instance.ip-address=${CF_INSTANCE_INTERNAL_IP}` --
  This is the IP address that can be seen by other applications
  running in the foundation.
  Note that it is defined by reference to a PAS-supplied
  environment variable which will be interpolated at run-time.
* `eureka.instance.secure-port-enabled=false` -- Eureka/Ribbon
  will try to use SSL connections by default.
  However, within the foundation only plain HTTP will work
  (although the mesh network may be securing traffic otherwise).
* `eureka.instance.non-secure-port-enabled=true` -- This is the
  complementary value to the one above, enabling non-secure
  usage.
* `eureka.instance.non-secure-port=${PORT}` -- This is the port
  via which other applications will access the application.
  It should be 8080, but the `PORT` environment variable
  contains the value in case something else has been set.
  
Once the properties have been added, and the application re-pushed,
the second thing that needs to be done is to add network policies
that enable C2C networking.
This can be done as follows:

```bash
for app in allocations backlog timesheets
  do cf add-network-policy tracker-$app --destination-app tracker-registration
done
```

The target port defaults to TCP port 8080 so this does not need to
be specified here.
Note that there is no need to create the reverse trust relationships
from `tracker-registration` to the the other applications as it does
not initiate any connections to them.

Use of C2C networking can be seen by looking at the logs of
`tracker-registration`.
Before this is added you can see requests from the other applications
going through the GoRouter (`RTR` lines in the logs) when, for example,
adding an allocation.
After the change, there are no more `RTR` messages shown.

It is also possible to delete the public route to the registration
server, and the other applications will continue to function.
However, if this route is deleted it will no longer be possible
to add new users or projects.
  

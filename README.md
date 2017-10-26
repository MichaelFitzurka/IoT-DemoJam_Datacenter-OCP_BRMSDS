# IoT-DemoJam_Datacenter-OCP_BRMSDS
Internet of Things DemoJam :: Datacenter OpenShift Container Platform :: Business Rules Management System Decision Server

---
Based on [PatrickSteiner/IoT_Demo_Datacenter](https://github.com/PatrickSteiner/IoT_Demo_Datacenter)  
Updated to run as microservices on OpenShift/CDK.

## Notes:
On OpenShift, my BRMS Decision Server also deploys a servlet which depends on another Data Grid for OpenShift pod for coordination.

So, first load and start the Data Grid for OpenShift pod with: [iotdj-docp-dg.json](https://github.com/MichaelFitzurka/IoT-DemoJam_Datacenter-OCP_BRMSDS/blob/master/config/iotdj-docp-dg.json)  
There are no passwords to set.

Then load and start BRMS Decision Server with: [iotdj-docp-bds.json](https://github.com/MichaelFitzurka/IoT-DemoJam_Datacenter-OCP_BRMSDS/blob/master/config/iotdj-docp-bds.json)  
There are 4 passwords to set for this pod:  
> KIE Server Password  
> IoT DemoJam Datacenter BPM Suite Intelligent Process Server Password  
> IoT DemoJam Datacenter Source A-MQ Broker Password  
> IoT DemoJam Datacenter Target MQTT Broker Password

I used "DLT-iot-123!" as the password consistently throughout, but feel free to use different passwords for each if you so desire.  
However, I did not reconfigure the BPMS system, so you will need to use Patrick Steiner's original password of "change12_me" for the "IoT DemoJam Datacenter BPM Suite Intelligent Process Server Password"

The other values in the templates are pre-configured to my IoT-DemoJam ecosystem, so leave as-is unless you have changed things.

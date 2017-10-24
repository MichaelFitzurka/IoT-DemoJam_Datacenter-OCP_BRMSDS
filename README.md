# IoT-DemoJam_Datacenter-OCP_BRMSDS
Internet of Things DemoJam :: Datacenter OpenShift Container Platform :: Business Rules Management System Decision Server

---
Based on [PatrickSteiner/IoT_Demo_Datacenter](https://github.com/PatrickSteiner/IoT_Demo_Datacenter)  
Updated to run as microservices on Openshift/CDK.

## Notes:
On Openshift, the BRMS Decision Server deploys a servlet which depends on another Data Grid for Openshift pod.

So, first load and start the Data Grid for Openshift pod with: [iotdj-docp-dg.json](https://github.com/MichaelFitzurka/IoT-DemoJam_Datacenter-OCP_BRMSDS/blob/master/config/iotdj-docp-dg.json)  
There are no passwords to set.

Then load and start BRMS Decision Server with: [iotdj-docp-bds.json](https://github.com/MichaelFitzurka/IoT-DemoJam_Datacenter-OCP_BRMSDS/blob/master/config/iotdj-docp-bds.json)  
There are 4 passwords to set for this pod:  
> KIE Server Password  
> IoT DemoJam Datacenter BPM Suite Intelligent Process Server Password  
> IoT DemoJam Datacenter Source A-MQ Broker Password  
> IoT DemoJam Datacenter Target MQTT Broker Password



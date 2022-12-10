# IoTCore

The IoTCore project provides a kick-start infrastructure setup for IoT related projects on top of AWS services for cloud connectivity and MongoDB as main database. The internal design follows the DDD pattern and is targeted for Microservice based systems, providing the neccessary interfaces and base classes in the main IoTCore module, as contracts for the infrastructure-dependent implementations provided in other modules.

## IoTCore Modules

The main project is divided into three modules: IoTCore, IoTCore-Aws, and IoTCore-Mongodb. The first one can be considered as the core package from a DDD model whereas the other two are pure infrastructure modules containing the implementation for the required DAO and Service classes. 

### IoTCore
This module exposes the interfaces and implementation-aware classes for implementing the entities, DAOs, and services in the domain layer. 
 
### IoTCore-Aws
AWS implementation and enabling classes for the services defined in the core module. 

### IoTDatamodel
MongoDB implementation for entities, DAOs, and other features from the core module.

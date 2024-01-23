# NaiveView
Naive View es un proyecto que implementa una arquitectura con un servidor centralizado, donde los clientes se conectan al iniciar, estableciendo una comunicación cliente-servidor mediante Sockets.  

Esta implementación sigue el concepto del 'Two Phase Commit' con un servidor central. Por lo tanto, nos ahorramos la trama del Done, ya que no es necesario notificar a todos los componentes de la red, solo al servidor central, ya que este es el 'bloqueador' de la actualización de los datos.  

El funcionamiento inicia cuando el cliente desea enviar una trama de Read al servidor, que simplemente le devuelve el valor entero almacenado (siempre correcto).  

En cambio, en la trama de Update, el cliente envía previamente una trama de 'Solicitud de Update'. El servidor es capaz de gestionar esta solicitud y devuelve un OK al cliente si los datos no están bloqueados por otra solicitud en ese momento; de lo contrario, devuelve un KO.  

Cuando el cliente recibe un KO, intenta reenviar esta solicitud hasta que recibe un OK. Al recibir un OK, implica que el servidor ya ha bloqueado esa variable, y luego el cliente realiza primero la lectura de los datos y luego puede realizar la actualización del valor (commit).  

Finalmente, el servidor, al recibir el valor, actualiza su variable y la desbloquea, esperando nuevas solicitudes.  
## Limitaciones:
### Robustesa:
La implementación centralizada presenta una limitación en la robustez debido al SPOF (Single Point of Failure) que implica el servidor central. Si el servidor experimenta problemas, todo el sistema se ve afectado. La replicación limitada de la variable en un solo punto también afecta la integridad.

### Limitación Económica:
La centralización del sistema implica costos económicos, ya que se requiere mantener en funcionamiento una máquina dedicada como servidor. Aunque esto podría mitigarse reduciendo el volumen de la red utilizada en la comunicación.

### Número de Clientes:
El número de clientes que pueden conectarse a un servidor puede verse limitado físicamente por el número de puertos disponibles. Sin embargo, esta limitación podría superarse implementando las comunicaciones mediante solicitudes HTTP en lugar de sockets.

### Orden de Atención a Clientes:
No se puede garantizar un orden de atención a los clientes basado en el principio de "First Come, First Serve", lo que implica que un cliente podría sufrir de "starvation".

### Cuello de Botella:
Existe la posibilidad de un cuello de botella, especialmente si algún cliente tarda mucho en enviar datos y el servidor está esperando con la variable bloqueada. Esta limitación podría abordarse parcialmente mediante el uso de timeouts.

##  
- @author: Oscar Julian - Bernat Segura  
- @date: October 2022

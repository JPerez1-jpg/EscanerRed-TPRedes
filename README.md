+ Escáner de Red - TP Redes

'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

- Inicio:

1) Abrir el proyecto en **Eclipse**
	
2) Ejecutar el archivo "Main.java" como una "Java Aplication"
	
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''		
	
- Parámetros del Escáner:

IP de Inicio: Es la IP por donde va a empezar el escaneo

IP de Fin: Es la última IP que va a entrar en el escaneo

*Utilizando las dos marcamos la extension de la busqueda de equipos, osea, cuanto va a abarcar el escaner*

Tiempo de espera: Es el tiempo, en este caso en milisegundos, que el programa va a esperar la respuesta de cada IP a su paquete de prueba
	
Reintentos: Acá se especifica la cantidad de veces que el mensaje al que le pusimos un tiempo de respuesta en "Tiempo de espera" se va a reenviar si no hubo respuesta, siendo generalmente una sola vez, para evitar demoras excesivas
	
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''	
	
- Vista del Escáner:

IP: La dirección ip del equipo
	
Nombre equipo: El nombre DNS del equipo (si no se puede conseguir, se mostrará como "Desconocido")

Activo: Casilla que marca si el equipo en cuestión está activo o no

Tiempo: El tiempo en milisegundos en que tardó en responder el equipo al programa
	
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
- Acciones del Escáner:

Iniciar Escaneo: Una vez que todas las variables son correctamente ingresadas, al tocar este botón se empezará el escaneo bajo esos parámetros
	
Detener Escaneo: A la mitad de un escaneo este botón se puede usar para frenar todo en seco
	
Limpiar: Limpia todos los resultados de la vista, y el sistema queda en espera de la proxima accion, como un reset
	
Guardar Resultados: Al tocar este botón se guardarán los resultados de la vista en tu dispositivo como un archivo csv que se puede ver facilmente en un bloc de notas
	
Mostrar Todos/Activos: Filtro para que el usuario elija si la vista le va a mostrar todos los equipos que encontró, o solo los que están activos
	
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
*Cosas a tomar en cuenta*:

1) Si la IP de Fin es menor a la de Inicio el escaneo no se ejecutará al no tener sus límites claros 
	
2) Si la IP de Inicio y de Fin son iguales, solo se encontrará el dispositivo que tenga esa IP compartida, el escaner terminará con solo ese equipo
	
3) Todas las variables tienen sus reglas:
	Las IP deben ser válidas
	El tiempo y los reintentos deben ser números enteros
	Y ninguna variable puede quedar vacía
	
4) Arriba de la barra de progreso hay un "Estado del Sistema", es importante ver lo que dice para saber en que proceso o estado estamos

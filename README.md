# Hablemos: hablemos de pastelería, de fútbol o de salud, pero HABLEMOS!

##Configuracion Inicial
1. Descargarse GIT BASH
2. Configurar el usuario de Github con los siguientes comandos:
	2.a. git config --global user.name "<tu_nombre>"
	2.b. git config --global user.email <tu_email>
3. Clonar el proyecto del repositorio: git clone git@github.com:Guidobere/hablemos.git
	3.a. Si llega a aparecer el error "Permission denied (publickey)" es porque: o tienen una clave ssh configurada en github pero no es la que corresponde 
		a su computadora o tienen generada una clave ssh en su computadora pero no la configuraron en github. Si llega a ocurrir algo de esto vayan a la
		configuracion de su perfil en github y fijense dentro de SSH Keys si tienen alguna clave configurada, en tal caso van a tener que abrir GIT GUI (que se
		descarga con el git bash) y en "Ayuda" elijan la opción "Ver clave SSH". Una vez que se abre les va a mostrar su clave SSH (si la crearon, si no, la crean),
		la copian y crean una nueva clave SSH en Github con esa clave que copiaron.

##Empezando a trabajar: branches, pull, commit, merge, push
1. Para trabajar es necesario que cada uno se cree un branch nuevo desde el master con el nombre de la funcionalidad que van a desarrollar. Esto se hace
	con el comando git checkout -b "<nombre_del_branch>".
2. Para hacer un commit lo primero que hay que hacer es ver los archivos que se modificaron para no subir cualquier cosa que pueda llegar a romper todo.
	Para esto es necesario el comando "git status" el cual muestra todos los archivos con diferencias. Si se quiere saber que exactamente fue lo que se modificó
	entonces usen el comando "git diff head" que lo que hace es comparar lo que tienen ustedes localmente con el repositorio remoto.
3. Una vez que tengan identificados los archivos que quieran subir sus modificaciones, tienen que usar el comando "git add <nombre_del_archivo>". Se pueden
	agregar varios archivos a la vez si se separan por espacios (también se puede hacer con el comando anterior "git diff head <nombre_del_archivo>").
	3.a Si hay partes de un archivo que no quieren que todos tengan modificado (quizas alguna configuracion propia de sus pcs) se usa el comando
		"git add -p <nombre_del_archivo>" y el bash se encarga de separarles las modificaciones en diferentes "pedazos (chunks)" que pueden agregar,
		ignorar o hasta volver a dividir si es muy grande.
4. Cuando tienen todo agregado (pueden verificar los archivos agregados con git status), usen el comando git commit -m "<mensaje_del_commit>".
5. Antes de hacer el push es necesario hacer un FETCH (git fetch origin) y un pull (git pull origin) del branch para evitar conflictos. Si hubo algun
	commit ya hecho en ese branch por otro compañero es probable que les salte un mensaje de "merge", se les va a abrir una ventana en negro con un mensaje
	de merge, lo unico que tienen que hacer es escribir ":wq" para guardar ese mensaje y seguir adelante. Lo ideal seria que se hagan el fetch y el pull
	ANTES de empezar a trabajar, pero como estamos haciendo cosas en conjunto es posible que esto ocurra.
6. Momento del push! con el comando "git push origin" suben todos los commits que tengan hechos localmente (puede ser mas de uno) al repositorio remoto.
	Cuando este pusheado, avisar o crear un Merge Request para sumar los cambios al branch master.

##Merge request (Pull requests en Github)
1. En la web del repo, en la pestaña de PULL REQUESTS, crean un nuevo pull request seleccionando el branch de origen (el de ustedes, el nuevo) y el branch
	de destino (master, generalmente). Le ponen un comentario y lo crean.
2. Algun otro compañero lo revisara y lo aceptara, concluyendo el ciclo de la funcionalidad, agregandose el nuevo codigo al branch principal.
	2.a. Si el que lo revisa ve que hay algun inconveniente, lo avisa para que el que haya trabajado en ese branch lo solucione o esa misma persona arregla
		el problema y lo pushea al branch que se iba a mergear.
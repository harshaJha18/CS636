		create table menu_sizes(
				id integer not null, 
				size_name varchar(30) not null,
				primary key (id),
				unique (size_name));
				
	   create table pizza_sizes(
				id integer not null, 
				size_name varchar(30) not null,
				primary key (id));

		create table pizza_orders(
				id integer not null, 
				room_number integer not null, 
				size_id integer not null, 
				day integer not null, 
				status integer not null, -- 1 , 2, 3 (see PizzaOrder.java)
				primary key(id),
				foreign key (size_id) references pizza_sizes(id));
						
		create table menu_toppings(
				id integer not null, 
				topping_name varchar(30) not null,
				primary key(id),
				unique (topping_name));
				
	   create table pizza_toppings(
				id integer not null, 
				order_id integer not null,
				topping_name varchar(30) not null,
				primary key(id),
				foreign key (order_id) references pizza_orders(id));
		
		create table pizza_sys_tab (
				next_menu_topping_id integer not null,
				next_menu_size_id integer not null,
				next_order_id integer not null, 
				next_pizza_topping_id integer not null, 
				next_pizza_size_id integer not null, 
				current_day integer not null);
		
		insert into pizza_sys_tab values (1, 1, 1, 1, 1 ,1);



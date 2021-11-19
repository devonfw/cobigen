	@Bean
	public CommandLineRunner setup(${variables.entityName}Service ${variables.entityName?lower_case}Service) {
	    return (args) -> {
	        log.info("Generating sample data");
	        ${variables.entityName?lower_case}Service.deleteAll${variables.entityName}s();
	        Map<String, String> ${variables.entityName?lower_case}s = new HashMap<>();
	        ${variables.entityName?lower_case}s.put("Cerroni", "Stefano");
	        ${variables.entityName?lower_case}s.put("Rossini", "Stefano");
	        ${variables.entityName?lower_case}s.put("Pironi", "Alessandro");


	        ${variables.entityName?lower_case}s.forEach((surname, name) ->
	                ${variables.entityName?lower_case}Service.save${variables.entityName}(${variables.entityName}.builder()
	                        .name(name)
	                        .surname(surname)
	                        .dateOfBirth(LocalDate.now())
	                        .build()));

	        ${variables.entityName?lower_case}Service.getAll${variables.entityName}s().forEach(${variables.entityName?lower_case} ->
	                log.info("${variables.entityName?upper_case} --> " + ${variables.entityName?lower_case}.getName() + " ID: " + ${variables.entityName?lower_case}.getId()));
	    };
	}
package com.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

public class JaxbPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getLogger().error("Applying JAXB Plugin to project [" + project.getName() + "]");

        try {
            JAXBContext context = JAXBContext.newInstance(EmployeesDocument.class);

            try (InputStream fis = JaxbPlugin.class.getClassLoader().getResourceAsStream("employees.xml")) {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                EmployeesDocument result = (EmployeesDocument) unmarshaller.unmarshal(fis);
                result.people.forEach(person -> System.out.println("Loaded person: " + person.name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        project.getSubprojects().forEach(subProject -> subProject.getPlugins().apply(JaxbPlugin.class));
    }

    @XmlRootElement(name = "employees")
    static class EmployeesDocument {

        @XmlElement(name = "person")
        private List<Person> people;
    }

    static class Person {

        @XmlElement(name = "name")
        private String name;
    }
}

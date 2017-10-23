package br.com.caelum.vraptor.boilerplate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.UUIDGenerator;

@MappedSuperclass
public abstract class UUIDEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="hibernate-uuid")
	@GenericGenerator(name="hibernate-uuid", strategy = "uuid2", parameters={
		@Parameter(name=UUIDGenerator.UUID_GEN_STRATEGY_CLASS, value="org.hibernate.id.uuid.StandardRandomStrategy")
	})
	@Column(length=36, nullable=false)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

package br.com.felmanc.integrationtests.wrapper;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.felmanc.integrationtests.vo.BookVO;

public class BookEmbeddedVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("bookVOList")
	List<BookVO> books;

	public BookEmbeddedVO() {}

	public List<BookVO> getBooks() {
		return books;
	}

	public void setBooks(List<BookVO> books) {
		this.books = books;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((books == null) ? 0 : books.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookEmbeddedVO other = (BookEmbeddedVO) obj;
		if (books == null) {
			if (other.books != null)
				return false;
		} else if (!books.equals(other.books))
			return false;
		return true;
	}
}

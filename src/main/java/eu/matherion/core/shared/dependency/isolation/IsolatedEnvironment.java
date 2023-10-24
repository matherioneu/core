package eu.matherion.core.shared.dependency.isolation;

public interface IsolatedEnvironment<A, R> {

    R run(A apply);

}

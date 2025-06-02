import java.util.Optional;

public record LinearSystem2x2Solution(Optional<Vector2D> optional, boolean isNonexistent, boolean isInfinite) {}

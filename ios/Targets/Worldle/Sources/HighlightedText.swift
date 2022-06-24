import SwiftUI

struct HighlightedText: View {
    var text: String
    var matching: String
    var hightlightColor: Color

    var body: some View {
        let split = text.caseInsensitiveComponents(separatedBy: matching)
        split.forEach {
            if $0.lowercased().starts(with: matching.lowercased()) {
                
            } else {
                
            }
            Text($0)
        }
        return split.reduce(Text("")) { a, b -> Text in
            
            guard !b.case else {
                return a + Text(b.dropFirst()).foregroundColor(hightlightColor)
            }
            return a + Text(b)
        }
    }
}

extension StringProtocol {
    func caseInsensitiveComponents<T>(separatedBy separator: T) -> [SubSequence] where T: StringProtocol, Index == String.Index {
        var index = startIndex
        return ((try? NSRegularExpression(pattern: NSRegularExpression.escapedPattern(for: String(separator)), options: .caseInsensitive))?
            .matches(in: String(self), range: NSRange(location: 0, length: utf16.count))
            .compactMap {
                guard let range = Range($0.range, in: String(self))
                else { return nil }
                defer { index = range.upperBound }
                return self[index..<range.lowerBound]
            } ?? []) + [self[index...]]
    }
}
